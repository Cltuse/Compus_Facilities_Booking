import request from '../utils/request';

// 违规记录相关API
export const violationAPI = {
    // 上报违规记录
    reportViolation: (data) => request.post('/violation/record', data),
    
    // 获取维护人员上报的违规记录
    getMaintainerViolations: (params) => 
        request.get('/violation/maintainer', { params }),
    
    // 获取用户的违规记录
    getUserViolations: (userId, params) => 
        request.get(`/violation/user/${userId}`, { params }),
    
    // 获取违规记录详情
    getViolationDetail: (id) => 
        request.get(`/violation/${id}`),

    approveViolation: (id, adminId, remark) =>
        request.post(`/violation/${id}/approve`, null, {
            params: { adminId, remark }
        }),

    rejectViolation: (id, adminId, remark) =>
        request.post(`/violation/${id}/reject`, null, {
            params: { adminId, remark }
        }),

    revokeViolation: (id, adminId, remark) =>
        request.post(`/violation/${id}/revoke`, null, {
            params: { adminId, remark }
        }),
    
    // 更新违规记录状态
    updateViolationStatus: (id, status) => 
        request.put(`/violation/${id}/status`, { status }),
    
    // 删除违规记录
    deleteViolation: (id) => 
        request.delete(`/violation/${id}`)
};

// 导出默认对象，方便按需导入
export default violationAPI;
