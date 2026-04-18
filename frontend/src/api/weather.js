import axios from 'axios';
import { apiBaseUrl } from '../config/env';

const BASE_URL = apiBaseUrl;

export const weatherAPI = {
    getWeather: (city = '北京') => {
        return axios.get(`${BASE_URL}/weather/get`, {
            params: { city }
        });
    },
    
    getAutoWeather: () => {
        return axios.get(`${BASE_URL}/weather/auto`);
    }
};
