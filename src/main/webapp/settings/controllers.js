require('../yuMailApp')

yuMailApp.controller('ConfigItemsController', [ '$scope', '$rootScope', 'AjaxService', '$location', '$controller', function($scope, $rootScope, AjaxService, $location, $controller) {
    'use strict';
    
    $controller('BaseController', {
		$scope : $scope
	});
    
    $rootScope.pageTitle = "Configuration Items";
    
    $scope.restUrl = "settings/";
    
    $scope.load = function() {
    	AjaxService.call($scope.restUrl, 'GET').success(function(data, status, headers, config) {
            $scope.items = data;
        });
    };
    
    $scope.init = function() {
    	$scope.load();
    };
    
    $scope.add = function(data, ev) {
	    if(!data) {
	    	data = {};
	    }
	    $rootScope.temp = {
            item : data
        };
	    $scope.openAsDialog('settings/add.html', ev, function() {
	        $scope.load();
	    });
	};
	
	$scope.deleteItem = function(item, $event) {
		$scope.confirmDialog({
			title: 'Are you sure to delete this ?',
			content: 'Configuration Item Name: ' + item.name,
			okLabel: 'Delete',
			cancelLabel: 'Cancel'
		}, $event, function() {
			AjaxService.call($scope.restUrl + item.id, 'DELETE').success(function(data, status, headers, config) {
                $scope.load();
            });
		});
    };
    
} ]);

yuMailApp.controller('ConfigItemController', [ '$scope', '$rootScope', 'AjaxService', '$controller', function($scope, $rootScope, AjaxService, $controller) {
    'use strict';
    
    $controller('BaseController', {
		$scope : $scope
	});
    
    $scope.restUrl = "settings/";
    
    $scope.init = function() {
        $scope.item = $rootScope.temp.item;
    };
    
    $scope.save = function() {
        AjaxService.call($scope.restUrl, 'POST', $scope.item).success(function(data, status, headers, config) {
        	$scope.item = data;
        });
    };
    
} ]);