import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  envDir: "../",
  server: {
    host: true,
    port: 3000,
    strictPort: true //vite will not automatically try next free port
  }
})
