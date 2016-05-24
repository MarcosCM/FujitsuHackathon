var app = angular.module("myApp", []);
app.controller("myCtrl", function($scope, $http) {

    //GET clases
    $http.get('https://maps.googleapis.com/maps/api/geocode/json?address=cuatro+caminos+santander&key=AIzaSyBb6qPOlPv8BXjQuBgUhH1bxK815o35nig')
    .success(function(data, status, headers, config) {
      if (data.status == "OK"){
        addMarker(data.results[0].geometry.location.lat,data.results[0].geometry.location.lng,'TITLE TEST','OK','http://placehold.it/350x150');
        $scope.ob = obJS;
      }
    })
    .error(function(error, status, headers, config) {
      console.log("Error occured");
    });
});
