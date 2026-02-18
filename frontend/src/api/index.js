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
export const equipmentAPI = {
    list: () => request.get('/equipment/list'),
    available: () => request.get('/equipment/available'),
    getById: (id) => request.get(`/equipment/${id}`),
    search: (keyword) => request.get('/equipment/search', { params: { keyword } }),
    // 分页接口
    listPage: (params) => request.get('/equipment/listPage', { params }),
    searchPage: (keyword, params) => request.get('/equipment/searchPage', {
        params: { keyword, ...params }
    }),
    create: (data) => request.post('/equipment', data),
    update: (id, data) => request.put(`/equipment/${id}`, data),
    updateStatus: (id, status) => request.put(`/equipment/${id}/status`, { status }),
    delete: (id) => request.delete(`/equipment/${id}`)
};

// 预约相关API
export const reservationAPI = {
    list: () => request.get('/reservation/list'),
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
    getByEquipmentId: (equipmentId) => request.get(`/maintenance/equipment/${equipmentId}`),
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
export const equipmentCategoryAPI = {
    list: () => request.get('/equipment-category/list'),
    active: () => request.get('/equipment-category/active'),
    getById: (id) => request.get(`/equipment-category/${id}`),
    create: (data) => request.post('/equipment-category', data),
    update: (id, data) => request.put(`/equipment-category/${id}`, data),
    delete: (id) => request.delete(`/equipment-category/${id}`),
    toggleStatus: (id) => request.put(`/equipment-category/${id}/toggle-status`),
    // 搜索接口
    search: (keyword) => request.get('/equipment-category/search', { params: { keyword } }),
    searchByName: (keyword) => request.get('/equipment-category/search/name', { params: { keyword } }),
    // 分页接口
    listPage: (params) => request.get('/equipment-category/page', { params }),
    searchPage: (keyword, params) => request.get('/equipment-category/search/page', {
        params: { keyword, ...params }
    }),
    searchByNamePage: (keyword, params) => request.get('/equipment-category/search/name/page', {
        params: { keyword, ...params }
    }),
    listByStatusPage: (status, params) => request.get('/equipment-category/status/page', {
        params: { status, ...params }
    })
};

