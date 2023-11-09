import { createRouter, createWebHistory } from 'vue-router'
import { authenticationStatus } from '@/stores/authentication-status'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'auth',
      redirect: to => {
        // Redirect to /home if authenticated and /login if not
        if(authenticationStatus().isAuthenticated) {
          return { path: '/home' }
        } else {
          return { path: '/login'}
        }
      }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    }
  ]
})

export default router
