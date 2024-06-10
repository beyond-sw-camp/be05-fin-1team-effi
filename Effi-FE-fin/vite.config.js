import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'

// https://github.com/vuetifyjs/vuetify-loader/tree/next/packages/vite-plugin
import vuetify from 'vite-plugin-vuetify'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
		vue(),
		VueDevTools(),
		vuetify({ autoImport: true }),
	],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
    build: {
    outDir: "../../Effi-FE-fin/src/main/resources/static"
  },
  server: {
    host: true,
    port: 5173, // 사용하고자 하는 포트 번호로 변경
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 백엔드 서버 URL
        changeOrigin: true,
        secure: false,
      },
    },
  }
});
