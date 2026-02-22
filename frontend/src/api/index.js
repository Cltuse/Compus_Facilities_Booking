import request from '../utils/request';

// 用户相关API
export const userAPI = {
    login: (data) => request.post('/user/login', data),
    register: (data) => request.post('/user/register', data),
    list: () => request.get('/user/list'),
    getById: (id) => request.get(`/user/${id}`),
    create: (data) => request.post('/user', data),
    update: (id, data) => request.put(`/user/${id}`, data),
    changePassword: (id, data) => request.post(`/user/${id}/change-password`, data),
    delete: (id) => request.delete(`/user/${id}`)
};

// 设备相关API
export const facilityAPI = {
    list: () => request.get('/facility/list'),
    available: () => request.get('/facility/available'),
    getById: (id) => request.get(`/facility/${id}`),
    search: (keyword) => request.get('/facility/search', { params: { keyword } }),
    // 分页接口
    listPage: (params) => request.get('/facility/listPage', { params }),
    searchPage: (keyword, params) => request.get('/facility/searchPage', {
        params: { keyword, ...params }
    }),
    create: (data) => request.post('/facility', data),
    update: (id, data) => request.put(`/facility/${id}`, data),
    updateStatus: (id, status) => request.put(`/facility/${id}/status`, { status }),
    delete: (id) => request.delete(`/facility/${id}`)
};

// 预约相关API
export const reservationAPI = {
    list: () => request.get('/reservation/list'),
    search: (keyword) => request.get('/reservation/search', { params: { keyword } }),
    getByUserId: (userId) => request.get(`/reservation/user/${userId}`),
    getPending: () => request.get('/reservation/pending'),
    getById: (id) => request.get(`/reservation/${id}`),
    create: (data) => request.post('/reservation', data),
    update: (id, data) => request.put(`/reservation/${id}`, data),
    approve: (id, data) => request.put(`/reservation/${id}/approve`, data),
    reject: (id, data) => request.put(`/reservation/${id}/reject`, data),
    cancel: (id) => request.put(`/reservation/${id}/cancel`),
    complete: (id) => request.put(`/reservation/${id}/complete`),
    delete: (id) => request.delete(`/reservation/${id}`)
};

// 维护相关API
export const maintenanceAPI = {
    list: () => request.get('/maintenance/list'),
    getByfacilityId: (facilityId) => request.get(`/maintenance/facility/${facilityId}`),
    getById: (id) => request.get(`/maintenance/${id}`),
    create: (data) => request.post('/maintenance', data),
    update: (id, data) => request.put(`/maintenance/${id}`, data),
    delete: (id) => request.delete(`/maintenance/${id}`)
};

// 通知相关API
export const noticeAPI = {
    list: () => request.get('/notice/list'),
    published: () => request.get('/notice/published'),
    getById: (id) => request.get(`/notice/${id}`),
    create: (data) => request.post('/notice', data),
    update: (id, data) => request.put(`/notice/${id}`, data),
    delete: (id) => request.delete(`/notice/${id}`)
};

// 设备类别相关API
export const facilityCategoryAPI = {
    list: () => request.get('/facility-category/list'),
    active: () => request.get('/facility-category/active'),
    getById: (id) => request.get(`/facility-category/${id}`),
    create: (data) => request.post('/facility-category', data),
    update: (id, data) => request.put(`/facility-category/${id}`, data),
    delete: (id) => request.delete(`/facility-category/${id}`),
    toggleStatus: (id) => request.put(`/facility-category/${id}/toggle-status`),
    // 搜索接口
    search: (keyword) => request.get('/facility-category/search', { params: { keyword } }),
    searchByName: (keyword) => request.get('/facility-category/search/name', { params: { keyword } }),
    // 分页接口
    listPage: (params) => request.get('/facility-category/page', { params }),
    searchPage: (keyword, params) => request.get('/facility-category/search/page', {
        params: { keyword, ...params }
    }),
    searchByNamePage: (keyword, params) => request.get('/facility-category/search/name/page', {
        params: { keyword, ...params }
    }),
    listByStatusPage: (status, params) => request.get('/facility-category/status/page', {
        params: { status, ...params }
    })
};