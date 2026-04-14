import axios from 'axios';

const BASE_URL = 'http://localhost:5681/api';

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