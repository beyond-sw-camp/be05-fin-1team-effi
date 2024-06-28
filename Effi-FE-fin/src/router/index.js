import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth';

import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import MyPageView from '../views/MyPageView.vue'

import SearchView from '../views/SearchView.vue'
import GroupParticipantsView from '../views/GroupParticipantsView.vue'
import GroupSchedulesView from '../views/GroupSchedulesView.vue'

import TagStatisticsView from '../views/TagStatisticsView.vue'
import AllSchedulesView from '../views/AllSchedulesView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true },
      props: (route) => ({
        selectedCategories: route.query.selectedCategories || [],
        searchResults: route.query.searchResults || [],
        selectedGroupId: route.query.selectedGroupId || [],
      }) //?
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: MyPageView,
      meta: { requiresAuth: true }
    },
    {
      path: '/search',
      name: 'search',
      component: SearchView,
      meta: { requiresAuth: true }
    },
    {
      path: '/tagstatistics',
      name: 'tagstatistics',
      component: TagStatisticsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/groupparticipants',
      name: 'groupparticipants',
      component: GroupParticipantsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/allschedules',
      name: 'allschedules',
      component: AllSchedulesView,
      meta: { requiresAuth: true }
    },
    {
      path: '/groupschedules',
      name: 'groupschedules',
      component: GroupSchedulesView,
      meta: { requiresAuth: true }
    }
  ]
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
      next('/login'); // 인증이 필요하고 사용자가 로그인되어 있지 않으면 로그인 페이지로 리디렉션합니다.
  } else {
      next(); // 그렇지 않으면 다음 라우트로 이동합니다.
  }
});

export default router
