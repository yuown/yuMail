var gulp = require('gulp')
var sass = require('node-sass')

var browserify = require('browserify')
var source = require('vinyl-source-stream')

gulp.task('browserify', function() {
    // Grabs the app.js file
    return browserify('./js/primary.js')
        // bundles it and creates a file called main.js
        .bundle()
        .pipe(source('all.js'))
        // saves it the public/js/ directory
        .pipe(gulp.dest('./js/'));
});

gulp.task('default', ['browserify']);
