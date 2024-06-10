import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import MyPageView from '../views/MyPageView.vue'
import { useAuthStore } from '../stores/auth';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: MyPageView
    }
  ]
});
//커밋을 위한 주석
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
      next('/login'); // 인증이 필요하고 사용자가 로그인되어 있지 않으면 로그인 페이지로 리디렉션합니다.
  } else {
      next(); // 그렇지 않으면 다음 라우트로 이동합니다.
  }
});

export default router
