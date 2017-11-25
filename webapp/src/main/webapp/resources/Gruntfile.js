/**
 * Gruntfile example
 */
module.exports = function(grunt) {

    var

        /**
        * JS files to add to index.html
        * @type {Array}
        */
        jsFiles = [
            'js/*.js',
            'js/**/*.js',
            // 'js/ngmin/**/*.js'
        ],

        componentsJsFiles = [
            'bower_components/jquery/dist/jquery.js',
            'bower_components/angular/angular.js',
            'bower_components/datetimepicker/build/jquery.datetimepicker.full.js',
            'bower_components/bootstrap/dist/js/bootstrap.js',
            'bower_components/angular-bootstrap/ui-bootstrap.js',
            'bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
            'bower_components/bootstrap-datepicker/dist/js/bootstrap-datepicker.js',
            'bower_components/bootstrap-daterangepicker/daterangepicker.js',
            'bower_components/angular-daterangepicker/js/angular-daterangepicker.js',
            'bower_components/angular-ui-router/release/angular-ui-router.js',
            'bower_components/lodash/dist/lodash.js',
            'bower_components/restangular/dist/restangular.js',
            'bower_components/moment/moment.js',
            'bower_components/angular-momentjs/angular-momentjs.js',
            'bower_components/angular-ui-grid/ui-grid.js',
            'bower_components/ng-facebook/ngFacebook.js',
            'bower_components/ngmap/build/scripts/ng-map.min.js'
        ],

        cssFiles = [

            'bower_components/fontawesome/css/font-awesome.css',
            'bower_components/bootstrap/dist/css/bootstrap.css',
            'bower_components/datetimepicker/jquery.datetimepicker.css',
            'bower_components/simple-line-icons/css/simple-line-icons.css',
            'bower_components/bootstrap-datepicker/dist/css/bootstrap-datepicker.css',
            'bower_components/bootstrap-daterangepicker/daterangepicker.css',
            'bower_components/angular-ui-grid/ui-grid.css',
            'css/*',
        ];

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        jsFiles: jsFiles,
        componentsJsFiles: componentsJsFiles,
        cssFiles: cssFiles,

        // JS Linter
        jshint: {
            files: ['Gruntfile.js', 'js/**/*.js'],
                options: {
                    // options here to override JSHint defaults
                    '-W097': true,
                    '-W087': true, // forgotten debugger statement
                    '-W055': true,
                    '-W030': true, // expected an assignment or function call and instead saw an expression:: due to 'use strict';
                    globals: {
                    progressJs: true,
                    jQuery: true,
                    EventSource: true,
                    console: true,
                    module: true,
                    document: true,
                    window: true,
                    angular: true,
                    describe: true,
                    beforeEach: true,
                    afterEach: true,
                    it: true,
                    inject: true,
                    expect: true,
                    browser: true,
                    element: true,
                    by: true,
                    spyOn: true,
                    remove: true,
                    sessionStorage: true,
                    localStorage: true,
                    runs: true,
                    GH: true,
                    setTimeout: true,
                    alert: true,
                    Routing: true,
                    Highcharts: true,
                    moment:true,
                    setInterval: true,
                    clearInterval:true,
                    _: true
                }
            }
        },

        // File Concatenation
        concat: {
            options: {
                separator: ';'
            },
            jsComp: {
                src: [componentsJsFiles],
                dest: 'dist/<%= pkg.name %>-components.js'
            },
            jsComptwo: {
                src: [jsFiles],
                dest: 'dist/<%= pkg.name %>-all.js'
            }
        },

        // CSS minifier
        cssmin: {
            combine: {
                files: {
                    'dist/<%= pkg.name %>-all.min.css': [cssFiles]
                }
            }
        },

        // AngularJS converter for minification
        ngmin: {
            controllers: {
                src: ['js/controllers/**/*.js'],
                dest: 'js/ngmin/controllers-all.js'
            },
//             directives: {
// //                expand: true,
// //                 cwd: '/',
//                 src: ['js/directives/**/*.js'],
//                 dest: 'js/ngmin/directives-all.js'
//             },
            services: {
                // cwd: '/',
                src: ['js/services/**/*.js'],
                dest: 'js/ngmin/services-all.js'
            },
            filters: {
                // cwd: '/',
                src: ['js/filters/**/*.js'],
                dest: 'js/ngmin/filters-all.js'
            }
        },

        // JS Minifier
        uglify: {
            my_target: {
                options: {
                    mangle: false
                },
                files: {
                   // 'dist/<%= pkg.name %>-components.min.js': [componentsJsFiles],
                    'dist/<%= pkg.name %>-all.min.js': [jsFiles]
                }
            }
        },

        // Include font files
        copy: {
            main: {
                files: [
                    {
                        expand: true, flatten: true, src: ['fonts/*.{eot,svg,ttf,woff,woff2}','bower_components/*/*.{eot,svg,ttf,woff,woff2}' ], dest: 'dist/fonts/', filter: 'isFile'
                    },
                    {
                        expand: true, flatten: true, src: ['fonts/*/*.{eot,svg,ttf,woff,woff2}','bower_components/*/*.{eot,svg,ttf,woff,woff2}' ], dest: 'dist/fonts/', filter: 'isFile'
                    },
                    {
                        expand: true, flatten: true, src: ['fonts/*.{eot,svg,ttf,woff,woff2}','bower_components/*/*.{eot,svg,ttf,woff,woff2}' ], dest: 'dist/fonts/', filter: 'isFile'
                    },
                    {
                        expand: true, flatten: true, src: ['fonts/*/*.{eot,svg,ttf,woff,woff2}','bower_components/*/*.{eot,svg,ttf,woff,woff2}' ], dest: 'dist/', filter: 'isFile'
                    },
                    {
                        expand: true, flatten: true, src: ['fonts/ui-grid.{eot,svg,ttf,woff,woff2}'], dest: 'dist/', filter: 'isFile'
                    },
                    {
                        expand: true, flatten: true, src: ['images/*'], dest: 'dist/images', filter: 'isFile'
                    },
                    {
                        expand: true, flatten: true, src: ['images/select2*'], dest: 'dist/', filter: 'isFile'
                    }
                ]
            }
        },

        // File listener
        watch: {
            javascript: {
                files: ['Gruntfile.js', '<%= jsFiles %>'],
                tasks: ['javascript']
            },
            css: {
                files: ['<%= cssFiles %>'],
                tasks: ['cssmin']
            }
        },

    });

    // Tasks
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-ngmin');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('default', ['cssmin', 'javascript', 'copy','watch' ]);

    grunt.registerTask('javascript', ['jshint', 'ngmin', 'concat', 'copy', 'uglify']);

};