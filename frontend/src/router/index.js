import { createRouter, createWebHistory } from 'vue-router'
import { clearAuth, getUserInfo, isAuthenticated } from '../utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'facility', name: 'AdminFacility', component: () => import('../views/admin/Facility.vue') },
      { path: 'facility-category', name: 'AdminFacilityCategory', component: () => import('../views/admin/FacilityCategory.vue') },
      { path: 'reservation', name: 'AdminReservation', component: () => import('../views/admin/Reservation.vue') },
      { path: 'maintenance', name: 'AdminMaintenance', component: () => import('../views/admin/Maintenance.vue') },
      { path: 'feedback', name: 'AdminFeedback', component: () => import('../views/admin/Feedback.vue') },
      { path: 'profile', name: 'AdminProfile', component: () => import('../views/admin/Profile.vue') },
      { path: 'rule-config', name: 'AdminRuleConfig', component: () => import('../views/admin/RuleConfig.vue') },
      { path: 'blacklist', name: 'AdminBlacklist', component: () => import('../views/admin/Blacklist.vue') },
      { path: 'operation-log', name: 'AdminOperationLog', component: () => import('../views/admin/OperationLog.vue') },
      { path: 'user', name: 'AdminUser', component: () => import('../views/admin/User.vue') },
      { path: 'violation-records', name: 'AdminViolationRecords', component: () => import('../views/admin/ViolationRecords.vue') },
      { path: 'notice', name: 'AdminNotice', component: () => import('../views/admin/Notice.vue') }
    ]
  },
  {
    path: '/user',
    name: 'UserLayout',
    component: () => import('../layouts/UserLayout.vue'),
    redirect: '/user/welcome',
    children: [
      { path: 'welcome', name: 'UserWelcome', component: () => import('../views/user/Welcome.vue') },
      { path: 'facility', name: 'UserFacility', component: () => import('../views/user/Facility.vue') },
      { path: 'facility/:id', name: 'UserFacilityDetail', component: () => import('../views/user/FacilityDetail.vue') },
      { path: 'my-reservation', name: 'MyReservation', component: () => import('../views/user/MyReservation.vue') },
      { path: 'violation-records', name: 'ViolationRecords', component: () => import('../views/user/ViolationRecords.vue') },
      { path: 'feedback', name: 'Feedback', component: () => import('../views/user/Feedback.vue') },
      { path: 'notice', name: 'UserNotice', component: () => import('../views/user/Notice.vue') },
      { path: 'profile', name: 'UserProfile', component: () => import('../views/user/Profile.vue') }
    ]
  },
  {
    path: '/maintainer',
    name: 'MaintainerLayout',
    component: () => import('../layouts/MaintainerLayout.vue'),
    redirect: '/maintainer/dashboard',
    children: [
      { path: 'dashboard', name: 'MaintainerDashboard', component: () => import('../views/maintainer/Dashboard.vue') },
      { path: 'maintenance', name: 'MaintainerMaintenance', component: () => import('../views/maintainer/maintenance.vue') },
      { path: 'facility', name: 'MaintainerFacility', component: () => import('../views/maintainer/Facility.vue') },
      { path: 'violation-report', name: 'MaintainerViolationReport', component: () => import('../views/maintainer/ViolationReport.vue') },
      { path: 'profile', name: 'MaintainerProfile', component: () => import('../views/maintainer/Profile.vue') }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

function resolveHomeByRole(role) {
  if (role === 'ADMIN') {
    return '/admin/dashboard'
  }
  if (role === 'MAINTAINER') {
    return '/maintainer/dashboard'
  }
  return '/user/welcome'
}

router.beforeEach((to, from, next) => {
  const loggedIn = isAuthenticated()
  const user = getUserInfo()

  if (to.path === '/login' || to.path === '/register') {
    if (loggedIn && user) {
      next(resolveHomeByRole(user.role))
      return
    }
    next()
    return
  }

  if (!loggedIn || !user) {
    clearAuth()
    next('/login')
    return
  }

  if (to.path.startsWith('/admin') && user.role !== 'ADMIN') {
    next(resolveHomeByRole(user.role))
    return
  }

  if (to.path.startsWith('/maintainer') && user.role !== 'MAINTAINER') {
    next(resolveHomeByRole(user.role))
    return
  }

  if (to.path.startsWith('/user') && !['TEACHER', 'STUDENT', 'USER'].includes(user.role)) {
    next(resolveHomeByRole(user.role))
    return
  }

  next()
})

export default router
