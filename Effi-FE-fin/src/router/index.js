import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
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
});
router.beforeEach((to, from, next) => {
  const accessToken = localStorage.getItem('accessToken')

  // to.meta.requiresAuth가 true이고 accessToken이 없는 경우 로그인 페이지로 리다이렉트합니다.
  if (to.meta.requiresAuth && !accessToken) {
    next({ name: 'login' }) // 로그인 페이지로 리다이렉트합니다.
  } else {
    next() // 다음 단계로 진행합니다.
  }
})

export default router
