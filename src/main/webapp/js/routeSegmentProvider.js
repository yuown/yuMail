require('./yuMailApp')

yuMailApp.config(function($routeSegmentProvider, $routeProvider) {

    $routeSegmentProvider.options.autoLoadTemplates = true;

    $routeSegmentProvider.
    	when('/home', 'home').
    	when('/home/proxies', 'home.proxies').
    	when('/home/browsers', 'home.browsers').
    	when('/home/urls', 'home.urls').
    	when('/home/tests', 'home.tests').
    	when('/home/tests/:id/steps', 'home.testSteps').
    	when('/home/tests/:id/runner', 'home.runner').
    	when('/home/pages', 'home.pages').
    	when('/home/pages/:id/elements', 'home.elements').
    	when('/home/configs', 'home.configs').
    	segment('home', {
	        templateUrl : 'home/tmpl.html',
	    });

    $routeSegmentProvider.
	    within('home').
		    segment('default', {
		    	'default': true,
		        templateUrl : 'home/default.html'
		    }).
	    	segment('proxies', {
		        templateUrl : 'proxies/tmpl.html'
		    }).
	    	segment('browsers', {
		        templateUrl : 'browsers/tmpl.html'
		    }).
	    	segment('urls', {
		        templateUrl : 'urls/tmpl.html'
		    }).
	    	segment('tests', {
		        templateUrl : 'tests/tmpl.html'
		    }).
            segment('testSteps', {
                templateUrl : 'tests/steps.html',
                dependencies: ['id']
            }).
            segment('runner', {
                templateUrl : 'tests/runner.html'
            }).
            segment('pages', {
                templateUrl : 'pages/tmpl.html'
            }).
            segment('elements', {
                templateUrl : 'elements/tmpl.html'
            }).
            segment('configs', {
                templateUrl : 'settings/tmpl.html'
            });
    
    $routeProvider.otherwise({redirectTo: '/home/configs'}); 
});