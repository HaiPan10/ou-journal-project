module.exports = {
  content: ["../resources/templates/**/*.{html,js}", "../resources/templates/*.{html,js}", "./node_modules/flowbite/**/*.js", "./main.css"],
  mode: 'jit',
  theme: {
    extend: {
      colors: {
        main: "#084ac4",
        'main-action': "#011187",
        'main-light': '#739dd9',
      }
    },
    
  },
  plugins: [
    require('flowbite/plugin')({
      charts: true,
    }),
  ]
}
