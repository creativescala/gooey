/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "docs/src/templates/*.html",
    "docs/src/css/*.css",
    "docs/target/docs/site/**/*.html",
    "examples/js/target/scala-3.2.2/gooey-examples-fastopt/main.js"
  ],
  theme: {
    extend: {
      width: {
        '128': '32rem',
        '132': '33rem',
        '144': '36rem'
      }
    },
    fontFamily: {
      sans: ['Source Sans Pro', 'sans-serif'],
      serif: ['Crimson Pro', 'serif']
    }
  },
  plugins: [],
}
