import request from '../utils/request';

// 用户端相关API
export const userClientAPI = {
    // 违规记录相关
    getMyViolationRecords: (userId, page = 0, size = 10) => 
        request.get(`/user-client/violation-records/${userId}`, { 
            params: { page, size } 
        }),
    
    getViolationRecordDetail: (id, userId) => 
        request.get(`/user-client/violation-records/${id}/detail`, { 
            params: { userId } 
        }),

    // 反馈管理相关
    getMyFeedbacks: (userId, page = 0, size = 10) => 
        request.get(`/user-client/feedbacks/${userId}`, { 
            params: { page, size } 
        }),
    
    submitFeedback: (data) => request.post('/user-client/feedbacks', data),
    
    getFeedbackDetail: (id, userId) => 
        request.get(`/user-client/feedbacks/${id}/detail`, { 
            params: { userId } 
        }),

    // 预约规则相关
    getAllRuleConfigs: () => request.get('/user-client/rule-configs'),
    
    getRuleConfigByCategory: (categoryId) => 
        request.get(`/user-client/rule-configs/category/${categoryId}`),
    
    getRuleDescription: (categoryId) => 
        request.get(`/user-client/rule-configs/${categoryId}/description`)
};