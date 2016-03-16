var gulp = require('gulp')
var sass = require('gulp-sass')
var cssimport = require("gulp-cssimport")

var browserify = require('browserify')
var source = require('vinyl-source-stream')

var clean = require('gulp-clean');

var cssImportOptions = {};

gulp.task('clean', function () {
	return gulp.src('dist/*')
		.pipe(clean({force: true}))
		.pipe(gulp.dest('dist'));
});

gulp.task('browserify', function() {
    return browserify({entries: ['./websrc/js/yuMailApp.js', 
                                 './websrc/js/httpProvider.js', 
                                 './websrc/js/primary.js', 
                                 './websrc/js/routeSegmentProvider.js', 
                                 './websrc/js/services.js', 
                                 './websrc/js/stateProvider.js', 
                                 './websrc/js/home/controllers.js',
                                 './websrc/js/settings/controllers.js' ]})
        .bundle()
        .pipe(source('all.js'))
        .pipe(gulp.dest('./dist/js/'));
});

gulp.task('copyangularcss', function(){
	return gulp.src('./node_modules/angular-material/angular-material.min.css')
			   .pipe(gulp.dest('./dist/css/'));
});

gulp.task('processcss', function(){
	return gulp.src('./websrc/css/yuMail.css')
			   .pipe(cssimport(cssImportOptions))
			   .pipe(gulp.dest('./dist/css/'));
});

gulp.task('copyfonts', function(){
	return gulp.src('./websrc/css/MaterialIcons-Regular.*')
			   .pipe(gulp.dest('./dist/css/'));
});

gulp.task('default', ['clean'], function() {
	gulp.start(['copyangularcss', 'processcss', 'copyfonts', 'browserify']);
});
