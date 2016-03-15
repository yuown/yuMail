require('./yuMailApp')

yuMailApp.run(['$rootScope', '$location', '$cookieStore', '$http', function($rootScope, $location, $cookieStore, $http) {
	$rootScope.globals = $cookieStore.get('globals') || {};
} ]);