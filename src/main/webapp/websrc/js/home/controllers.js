(function () {
    'use strict';
    
    angular.module('yuMailApp').controller('HomeController', [ '$scope', '$rootScope', '$timeout', '$mdSidenav', 'AuthenticationService', '$location', '$controller', function($scope, $rootScope, $timeout, $mdSidenav, AuthenticationService, $location, $controller) {
	    'use strict';
	    
	    $scope.app = {
	    	name: "yuMail",
	    	icon: "menu"
	    };
	
	    $scope.urls = [ {
			url : "#/home/configs",
			title : "Configuration Items",
			icon : ""
		} ];
	    
	    $controller('BaseController', {
			$scope : $scope
		});
	    
	    $scope.toggleLeft = buildDelayedToggler('left');
	
	    function buildDelayedToggler(navID) {
			return debounce(function() {
				$mdSidenav(navID).toggle();
			}, 200);
		}
	    
	    function debounce(func, wait, context) {
			var timer;
			return function debounced() {
				var context = $scope, args = Array.prototype.slice
						.call(arguments);
				$timeout.cancel(timer);
				timer = $timeout(function() {
					timer = undefined;
					func.apply(context, args);
				}, wait || 10);
			};
		}
	    
	} ]);
	
    angular.module('yuMailApp').controller('LeftController', [ '$scope', '$timeout', '$mdSidenav', function($scope, $timeout, $mdSidenav) {
		'use strict';
	
		$scope.close = function() {
			$mdSidenav('left').close();
		};
	
	} ]);
	
    angular.module('yuMailApp').controller('DefaultController', [ '$scope', '$timeout', '$mdSidenav', 'AjaxService', '$rootScope', '$mdDialog', function($scope, $timeout, $mdSidenav, AjaxService, $rootScope, $mdDialog) {
		'use strict';
	
		var millisPerDay = 1000 * 60 * 60 * 24;
	
	    $scope.init = function() {
	    	
	    };
	    
	    $scope.load = function() {
	    	
	    };
		
	} ]);
})();