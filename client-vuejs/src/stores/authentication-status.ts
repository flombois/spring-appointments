import { ref } from 'vue'
import { defineStore } from 'pinia'

export const authenticationStatus = defineStore("authentication-status", () => {
    const isAuthenticated = ref(false)
    return { isAuthenticated }
});