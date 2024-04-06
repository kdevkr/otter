/* eslint-env node */

// Configuration for your app
// https://v2.quasar.dev/quasar-cli-vite/quasar-config-js

const { mergeConfig } = require('vite')
const { configure } = require('quasar/wrappers');
const { transformAssetUrls } = require('@quasar/vite-plugin')
const path = require('path');

module.exports = configure(function (ctx) {
  return {
    // https://v2.quasar.dev/quasar-cli-vite/prefetch-feature
    preFetch: true,

    // app boot file (/src/boot)
    // --> boot files are part of "main.js"
    // https://v2.quasar.dev/quasar-cli-vite/boot-files
    boot: [
      'i18n',
      'axios',
      'defaults'
    ],

    // https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#css
    css: [
      'app.scss'
    ],

    // https://github.com/quasarframework/quasar/tree/dev/extras
    extras: [
      'roboto-font',
      'material-icons',
      'fontawesome-v6'
    ],

    // Full list of options: https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#build
    build: {
      target: {
        browser: [ 'es2019', 'edge88', 'firefox78', 'chrome87', 'safari13.1' ],
        node: 'node20'
      },

      vueRouterMode: 'history', // available values: 'hash', 'history'
      rebuildCache: true, // rebuilds Vite/linter/etc cache on startup
      publicPath: '/',
      minify: true,
      polyfillModulePreload: true,

      extendViteConf (viteConf) {
        viteConf.build = mergeConfig(viteConf.build, {
          emptyOutDir: true,
          manifest: true,
          minify: true,

          rollupOptions: {
            output: {
              assetFileNames: (assetInfo) => {
                if (assetInfo.name.match(new RegExp(/.*\.(ttf|woff|woff2)$/))) {
                  return "assets/[name][extname]" // if font files,
                }
                return 'assets/[name]-[hash][extname]' // default
              }
            }
          }
        })
      },
      viteVuePluginOptions: {
        template: {  transformAssetUrls }
      },

      vitePlugins: [
        ['@intlify/vite-plugin-vue-i18n', {
          runtimeOnly: false,
          include: path.resolve(__dirname, './src/i18n/**')
        }],
        ['vite-plugin-checker', {
          eslint: {
            lintCommand: 'eslint "./**/*.{js,mjs,cjs,vue}"'
          }
        }, { server: false }],
      ]
    },

    // Full list of options: https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#devServer
    devServer: {
      // https: true
      open: true // opens browser window automatically
    },

    // https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#framework
    framework: {
      config: {},

      autoImportComponentCase: 'combined',

      iconSet: 'svg-material-icons', // Quasar icon set
      lang: 'en-US', // Quasar language pack

      // Quasar plugins
      plugins: ['Loading', 'LoadingBar', 'Notify']
    },

    // https://v2.quasar.dev/options/animations
    animations: 'all', // --- includes all animations

    // https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#property-sourcefiles
    sourceFiles: {
      rootComponent: 'src/App.vue',
      router: 'src/router/index',
      store: 'src/store/index',
      // registerServiceWorker: 'src-pwa/register-service-worker',
      // serviceWorker: 'src-pwa/custom-service-worker',
    },
  }
});
