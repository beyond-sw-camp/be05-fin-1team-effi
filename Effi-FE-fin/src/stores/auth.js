import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore('auth', () => {
    const empNo = ref(null);
    const name = ref(null);
    const rank = ref(null);
    const accessToken = ref(null);
    const refreshToken = ref(null);

    const login = (empNoValue, nameValue, rankValue, accessTokenValue, refreshTokenValue) => {
        empNo.value = empNoValue;
        name.value = nameValue;
        rank.value = rankValue;
        accessToken.value = accessTokenValue;
        refreshToken.value = refreshTokenValue;
        sessionStorage.setItem('empNo', empNoValue);
        sessionStorage.setItem('name', nameValue);
        sessionStorage.setItem('rank', rankValue);
        sessionStorage.setItem('accessToken', accessTokenValue);
        sessionStorage.setItem('refreshToken', refreshTokenValue);
        console.log('Session stored:', {
            empNo: empNoValue,
            name: nameValue,
            rank: rankValue,
            accessToken: accessTokenValue,
            refreshToken: refreshTokenValue
        });
    };

    const logout = () => {
        empNo.value = null;
        name.value = null;
        rank.value = null;
        accessToken.value = null;
        refreshToken.value = null;
        sessionStorage.removeItem('empNo');
        sessionStorage.removeItem('name');
        sessionStorage.removeItem('rank');
        sessionStorage.removeItem('accessToken');
        sessionStorage.removeItem('refreshToken');
    };

    const loadSession = () => {
        const empNoValue = sessionStorage.getItem('empNo');
        const nameValue = sessionStorage.getItem('name');
        const rankValue = sessionStorage.getItem('rank');
        const accessTokenValue = sessionStorage.getItem('accessToken');
        const refreshTokenValue = sessionStorage.getItem('refreshToken');
        console.log('Values loaded from sessionStorage:', {
            empNo: empNoValue,
            name: nameValue,
            rank: rankValue,
            accessToken: accessTokenValue,
            refreshToken: refreshTokenValue
        });
        if (accessTokenValue && refreshTokenValue && empNoValue && nameValue && rankValue) {
            login(empNoValue, nameValue, rankValue, accessTokenValue, refreshTokenValue);
        } else {
            console.log('No valid session found');
        }
    };

    const isLoggedIn = computed(() => !!accessToken.value);

    return {
        empNo,
        name,
        rank,
        accessToken,
        refreshToken,
        login,
        logout,
        loadSession,
        isLoggedIn
    };
});
