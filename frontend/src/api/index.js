import request from '../utils/request';

// 鐢ㄦ埛鐩稿叧API
export const userAPI = {
    login: (data) => request.post('/user/login', data),
    register: (data) => request.post('/user/register', data),
    updateProfile: (data) => request.put('/user/me', data),
    list: (params) => request.get('/user/list', { params }),
    getById: (id) => request.get(`/user/${id}`),
    create: (data) => request.post('/user', data),
    update: (id, data) => request.put(`/user/${id}`, data),
    changePassword: (id, data) => request.post(`/user/${id}/change-password`, data),
    delete: (id) => request.delete(`/user/${id}`),
    searchUsers: (keyword) => request.get('/user/search', { params: { keyword } })
};

// 绠＄悊鍛樺弽棣堢浉鍏矨PI
export const feedbackAPI = {
    // 鑾峰彇鎵€鏈夊弽棣?
    getFeedbacks: (page = 0, size = 10) => 
        request.get('/feedback/list', { 
            params: { page, size } 
        }),
    
    // 鑾峰彇鍙嶉璇︽儏
    getFeedbackDetail: (id) => 
        request.get(`/feedback/${id}`),
    
    // 鍥炲鍙嶉
    replyFeedback: (id, data) => request.post(`/feedback/${id}/reply`, null, {
        params: {
            reply: data.replyContent
        }
    }),
    updateFeedbackStatus: (id, status) =>
        request.put(`/feedback/${id}/status`, null, { params: { status } }),
    
    // 鍒犻櫎鍙嶉
    deleteFeedback: (id) => 
        request.delete(`/feedback/${id}`)
};

// 璁惧鐩稿叧API
export const facilityAPI = {
    list: () => request.get('/facility/list'),
    available: () => request.get('/facility/available'),
    getById: (id) => request.get(`/facility/${id}`),
    getDetail: (id, days = 7) => request.get(`/facility/${id}/detail`, { params: { days } }),
    search: (keyword) => request.get('/facility/search', { params: { keyword } }),
    // 鍒嗛〉鎺ュ彛
    listPage: (params) => request.get('/facility/listPage', { params }),
    searchPage: (keyword, params) => request.get('/facility/searchPage', {
        params: { keyword, ...params }
    }),
    create: (data) => request.post('/facility', data),
    createWithImage: (data, imageFile) => {
        const formData = new FormData();
        formData.append('facility', new Blob([JSON.stringify(data)], { type: 'application/json' }));
        if (imageFile) {
            formData.append('image', imageFile);
        }
        return request.post('/facility', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
    update: (id, data) => request.put(`/facility/${id}`, data),
    updateStatus: (id, status) => request.put(`/facility/${id}/status`, { status }),
    delete: (id) => request.delete(`/facility/${id}`),
    // 鍥剧墖鐩稿叧鎺ュ彛
    uploadImage: (idOrFile, file) => {
        if (file) {
            const formData = new FormData();
            formData.append('file', file);
            return request.post(`/facility/${idOrFile}/image`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
        } else {
            const formData = new FormData();
            formData.append('file', idOrFile);
            return request.post('/file/upload/facility', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
        }
    },
    deleteImage: (id) => request.delete(`/facility/${id}/image`)
};

// 棰勭害鐩稿叧API
export const reservationAPI = {
    list: () => request.get('/reservation/list'),
    search: (keyword) => request.get('/reservation/search', { params: { keyword } }),
    searchPage: (keyword, params) => request.get('/reservation/search/page', {
        params: { keyword, ...params }
    }),
    getByUserId: (userId) => request.get(`/reservation/user/${userId}`),
    getPending: () => request.get('/reservation/pending'),
    getById: (id) => request.get(`/reservation/${id}`),
    create: (data) => request.post('/reservation', data),
    update: (id, data) => request.put(`/reservation/${id}`, data),
    approve: (id, data) => request.put(`/reservation/${id}/approve`, data),
    reject: (id, data) => request.put(`/reservation/${id}/reject`, data),
    cancel: (id) => request.put(`/reservation/${id}/cancel`),
    complete: (id) => request.put(`/reservation/${id}/complete`),
    delete: (id) => request.delete(`/reservation/${id}`),
    getStatsByTimeRange: (range) => request.get('/reservation/stats/time-range', { params: { range } }),
    getCategoryStats: (range) => request.get('/reservation/stats/category', { params: { range } }),
    // 绛惧埌绛鹃€€鐩稿叧鎺ュ彛
    checkin: (id) => request.put(`/reservation/${id}/checkin`),
    checkout: (id) => request.put(`/reservation/${id}/checkout`),
    verify: (id, verificationCode) => request.put(`/reservation/${id}/verify`, null, { params: { verificationCode } }),
    getVerificationCode: (id) => request.get(`/reservation/${id}/verification-code`),
    checkAvailability: (facilityId, startTime, endTime) => request.get('/reservation/availability', {
        params: { facilityId, startTime, endTime }
    })
};

// 缁存姢鐩稿叧API
export const maintenanceAPI = {
    list: () => request.get('/maintenance/list'),
    getByfacilityId: (facilityId) => request.get(`/maintenance/facility/${facilityId}`),
    getByMaintainerId: (maintainerId) => request.get(`/maintenance/maintainer/${maintainerId}`),
    getById: (id) => request.get(`/maintenance/${id}`),
    create: (data) => request.post('/maintenance', data),
    update: (id, data) => request.put(`/maintenance/${id}`, data),
    complete: (id, data) => request.put(`/maintenance/${id}/complete`, data),
    delete: (id) => request.delete(`/maintenance/${id}`),
    getStatsByTimeRange: (range) => request.get('/maintenance/stats/time-range', { params: { range } }),
    getTypeDistribution: (range) => request.get('/maintenance/stats/type-distribution', { params: { range } }),
    getDurationStats: (range) => request.get('/maintenance/stats/duration', { params: { range } }),
    getFacilityFaultStats: (range) => request.get('/maintenance/stats/facility-faults', { params: { range } }),
    getSummaryStats: () => request.get('/maintenance/stats/summary')
};

// 閫氱煡鐩稿叧API
export const noticeAPI = {
    list: (params) => request.get('/notice/list', { params }),
    published: () => request.get('/notice/published'),
    getById: (id) => request.get(`/notice/${id}`),
    create: (data) => request.post('/notice', data),
    update: (id, data) => request.put(`/notice/${id}`, data),
    delete: (id) => request.delete(`/notice/${id}`)
};

// 璁惧绫诲埆鐩稿叧API
export const facilityCategoryAPI = {
    list: () => request.get('/facility-category/list'),
    active: () => request.get('/facility-category/active'),
    getById: (id) => request.get(`/facility-category/${id}`),
    create: (data) => request.post('/facility-category', data),
    update: (id, data) => request.put(`/facility-category/${id}`, data),
    delete: (id) => request.delete(`/facility-category/${id}`),
    toggleStatus: (id) => request.put(`/facility-category/${id}/toggle-status`),
    // 鎼滅储鎺ュ彛
    search: (keyword) => request.get('/facility-category/search', { params: { keyword } }),
    searchByName: (keyword) => request.get('/facility-category/search/name', { params: { keyword } }),
    // 鍒嗛〉鎺ュ彛
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

// 鏂囦欢涓婁紶鐩稿叧API
export const fileAPI = {
    uploadFacilityImage: (facilityId, file) => {
        const formData = new FormData();
        formData.append('file', file);
        return request.post(`/file/upload/facility/${facilityId}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
    uploadAvatar: (file) => {
        const formData = new FormData();
        formData.append('file', file);
        return request.post('/file/upload/avatar', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
    deleteFile: (fileUrl) => request.delete('/file/delete', { params: { fileUrl } })
};

// 绠＄悊鍛樹笓灞炲姛鑳紸PI
export const adminAPI = {
    // 棰勭害瑙勫垯閰嶇疆绠＄悊
    getRuleConfigs: () => request.get('/admin/rule-configs'),
    getRuleConfigHistory: (categoryId) => request.get('/admin/rule-configs/history', { params: { categoryId } }),
    createRuleConfig: (data) => request.post('/admin/rule-configs', data),
    getRuleConfigById: (id) => request.get(`/admin/rule-configs/${id}`),
    
    // 榛戝悕鍗曠鐞?
    getBlacklist: (params) => request.get('/admin/blacklist', { params }),
    addToBlacklist: (data) => request.post('/admin/blacklist', data),
    removeFromBlacklist: (id) => request.put(`/admin/blacklist/${id}/remove`),
    autoExpireBlacklist: () => request.put('/admin/blacklist/auto-expire'),
    
    // 鎿嶄綔鏃ュ織瀹¤
    getOperationLogs: (params) => request.get('/admin/operation-logs', { params }),
    getOperationLogById: (id) => request.get(`/admin/operation-logs/${id}`),
    getOperationTypes: () => request.get('/admin/operation-logs/types'),
    
    // 鐢ㄦ埛鎼滅储锛堢敤浜庨粦鍚嶅崟娣诲姞锛?
    searchUsers: (keyword) => request.get('/user/search', { params: { keyword } }),
    
    // 杩濊璁板綍绠＄悊
    getAllViolations: (page = 0, size = 10, userName = '', violationType = '', status = '') => 
        request.get('/violation/all', { params: { page, size, userName, violationType, status } }),
    recordViolation: (data) => request.post('/violation/record', data),
    approveViolation: (id, remark) => request.post(`/violation/${id}/approve`, null, {
        params: { remark }
    }),
    rejectViolation: (id, remark) => request.post(`/violation/${id}/reject`, null, {
        params: { remark }
    }),
    revokeViolation: (id, remark) => request.post(`/violation/${id}/revoke`, null, {
        params: { remark }
    }),
    updateViolationStatus: (id, status) => request.put(`/violation/${id}/status`, null, {
        params: { status }
    }),
    
    // 缁熻鏁版嵁
    getDashboardStats: () => request.get('/admin/stats/dashboard'),
    getUserStats: () => request.get('/admin/stats/users'),
    getReservationStats: () => request.get('/admin/reservation-stats'),
    getReservationTrends: () => request.get('/admin/reservation-trends'),
    getFacilityStats: () => request.get('/admin/stats/facilities')
};

// 杩濊璁板綍绠＄悊API
export const violationAPI = {
    getAllViolations: (page = 0, size = 10, userName = '', violationType = '', status = '') => 
        request.get('/violation/all', { params: { page, size, userName, violationType, status } }),
    recordViolation: (data) => request.post('/violation/record', data),
    approveViolation: (id, remark) => request.post(`/violation/${id}/approve`, null, {
        params: { remark }
    }),
    rejectViolation: (id, remark) => request.post(`/violation/${id}/reject`, null, {
        params: { remark }
    }),
    revokeViolation: (id, remark) => request.post(`/violation/${id}/revoke`, null, {
        params: { remark }
    }),
    updateViolationStatus: (id, status) => request.put(`/violation/${id}/status`, null, {
        params: { status }
    }),
    getUserCurrentCreditScore: (userId) => request.get(`/violation/user/${userId}/credit-score`),
    getUserViolationCount: (userId) => request.get(`/violation/user/${userId}/violation-count`),
    getViolationStats: () => request.get('/violation/stats')
};

// 鐢ㄦ埛绔浉鍏矨PI
export const userClientAPI = {
    // 杩濊璁板綍鐩稿叧
    getMyViolationRecords: (userId, page = 0, size = 10) => 
        request.get(`/user-client/violation-records/${userId}`, { 
            params: { page, size } 
        }),
    
    getViolationRecordDetail: (id, userId) => 
        request.get(`/user-client/violation-records/${id}/detail`, { 
            params: { userId } 
        }),

    // 鍙嶉绠＄悊鐩稿叧
    getMyFeedbacks: (userId, page = 0, size = 10) => 
        request.get(`/user-client/feedbacks/${userId}`, { 
            params: { page, size } 
        }),
    
    submitFeedback: (data) => request.post('/user-client/feedbacks', data),
    
    getFeedbackDetail: (id, userId) => 
        request.get(`/user-client/feedbacks/${id}/detail`, { 
            params: { userId } 
        }),

    // 棰勭害瑙勫垯鐩稿叧
    getAllRuleConfigs: () => request.get('/user-client/rule-configs'),
    
    getRuleConfigByCategory: (categoryId) => 
        request.get(`/user-client/rule-configs/category/${categoryId}`),
    
    getRuleDescription: (categoryId) => 
        request.get(`/user-client/rule-configs/${categoryId}/description`)
};



