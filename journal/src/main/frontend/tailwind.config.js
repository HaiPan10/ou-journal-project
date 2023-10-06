module.exports = {
  content: ["../resources/templates/**/*.{html,js}", "../resources/templates/*.{html,js}", "./node_modules/flowbite/**/*.js"],
  mode: 'jit',
  theme: {
    extend: {
      colors: {
        main: "#084ac4",
        'main-action': "#063793",
      }
    },
    
  },
  plugins: [
    require('flowbite/plugin')({
      charts: true,
    }),
  ]
}
