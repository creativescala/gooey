/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["docs/src/templates/*.html", "docs/src/css/*.css", "docs/target/docs/site/**/*.html", "docs/target/docs/site/**/*.js"],
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
