module.exports = {
  content: ["../resources/templates/**/*.{html,js}", "../resources/templates/*.{html,js}", "./node_modules/flowbite/**/*.js", "./main.css"],
  mode: 'jit',
  theme: {
    container: {
      padding: {
        DEFAULT: '0.5rem',
        sm: '0rem',
        lg: '0rem',
        xl: '0rem',
        '2xl': '0rem',
      },
    },
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
