import { createRouter, createWebHistory } from 'vue-router';

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
            {
                path: 'dashboard',
                name: 'AdminDashboard',
                component: () => import('../views/admin/Dashboard.vue')
            },
            {
                path: 'equipment',
                name: 'AdminEquipment',
                component: () => import('../views/admin/Equipment.vue')
            },
            {
                path: 'equipment-category',
                name: 'AdminEquipmentCategory',
                component: () => import('../views/admin/EquipmentCategory.vue')
            },
            {
                path: 'reservation',
                name: 'AdminReservation',
                component: () => import('../views/admin/Reservation.vue')
            },
            {
                path: 'maintenance',
                name: 'AdminMaintenance',
                component: () => import('../views/admin/Maintenance.vue')
            },
            {
                path: 'profile',
                name: 'AdminProfile',
                component: () => import('../views/admin/Profile.vue')
            },
            {
                path: 'user',
                name: 'AdminUser',
                component: () => import('../views/admin/User.vue')
            },
            {
                path: 'notice',
                name: 'AdminNotice',
                component: () => import('../views/admin/Notice.vue')
            },
            {
                path: 'profile',
                name: 'AdminProfile',
                component: () => import('../views/admin/Profile.vue')
            }
        ]
    },
    {
        path: '/user',
        name: 'UserLayout',
        component: () => import('../layouts/UserLayout.vue'),
        redirect: '/user/welcome',
        children: [
            {
                path: 'welcome',
                name: 'UserWelcome',
                component: () => import('../views/user/Welcome.vue')
            },
            {
                path: 'equipment',
                name: 'UserEquipment',
                component: () => import('../views/user/Equipment.vue')
            },
            {
                path: 'my-reservation',
                name: 'MyReservation',
                component: () => import('../views/user/MyReservation.vue')
            },
            {
                path: 'notice',
                name: 'UserNotice',
                component: () => import('../views/user/Notice.vue')
            },
            {
                path: 'profile',
                name: 'UserProfile',
                component: () => import('../views/user/Profile.vue')
            }
        ]
    },
    {
        path: '/',
        redirect: '/login'
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
    const userInfo = localStorage.getItem('userInfo');

    if (to.path === '/login' || to.path === '/register') {
        next();
    } else if (!userInfo) {
        next('/login');
    } else {
        const user = JSON.parse(userInfo);
        if (to.path.startsWith('/admin') && user.role !== 'ADMIN') {
            next('/user/welcome');
        } else if (to.path.startsWith('/user') && user.role !== 'USER') {
            next('/admin/dashboard');
        } else {
            next();
        }
    }
});

export default router;
