import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
});

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 从localStorage获取用户信息
        const userInfo = localStorage.getItem('userInfo');
        if (userInfo) {
            // 对中文用户信息进行Base64编码以避免HTTP请求头编码问题
            const encodedUserInfo = btoa(unescape(encodeURIComponent(userInfo)));
            config.headers['User-Info'] = encodedUserInfo;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data;

        if (res.code === 200) {
            return res;
        } else {
            ElMessage.error(res.message || '请求失败');
            return Promise.reject(new Error(res.message || '请求失败'));
        }
    },
    error => {
        ElMessage.error(error.message || '网络错误');
        return Promise.reject(error);
    }
);

export default request;
