angular
    .module('app.controllers')
    .controller('heatmapController', function ($scope, QueryServices, NgMap) {

        $scope.heatmap;
        var vm = this;

        var datas;
        $scope.heatmapData = [];

        NgMap.getMap().then(function(map) {
            vm.map = map;
            $scope.heatmap = vm.map.heatmapLayers.foo;
        });

        $scope.toggleHeatmap= function(event) {
            $scope.heatmap.setMap($scope.heatmap.getMap() ? null : vm.map);
        };

        $scope.changeGradient = function() {
            var gradient = [
                'rgba(0, 255, 255, 0)',
                'rgba(0, 255, 255, 1)',
                'rgba(0, 191, 255, 1)',
                'rgba(0, 127, 255, 1)',
                'rgba(0, 63, 255, 1)',
                'rgba(0, 0, 255, 1)',
                'rgba(0, 0, 223, 1)',
                'rgba(0, 0, 191, 1)',
                'rgba(0, 0, 159, 1)',
                'rgba(0, 0, 127, 1)',
                'rgba(63, 0, 91, 1)',
                'rgba(127, 0, 63, 1)',
                'rgba(191, 0, 31, 1)',
                'rgba(255, 0, 0, 1)'
            ];
            $scope.heatmap.set('gradient', $scope.heatmap.get('gradient') ? null : gradient);
        };

        $scope.changeRadius = function() {
            $scope.heatmap.set('radius', $scope.heatmap.get('radius') ? null : 0.2);
        };
        $scope.changeOpacity = function() {
            $scope.heatmap.set('opacity', $scope.heatmap.get('opacity') ? null : 0.2);
        };

        var fake = [
            {location: new google.maps.LatLng(37.782, -122.447), weight: 0.5},
            {location: new google.maps.LatLng(37.782, -122.443), weight: 2},
            {location: new google.maps.LatLng(37.782, -122.441), weight: 3},
            {location: new google.maps.LatLng(37.782, -122.439), weight: 2},
            {location: new google.maps.LatLng(37.782, -122.435), weight: 0.5},
            {location: new google.maps.LatLng(37.785, -122.447), weight: 3},
            {location: new google.maps.LatLng(37.785, -122.445), weight: 2},
            {location: new google.maps.LatLng(37.785, -122.441), weight: 0.5},
            {location: new google.maps.LatLng(37.785, -122.437), weight: 2},
            {location: new google.maps.LatLng(37.785, -122.435), weight: 3}
        ];

        QueryServices.getPrices()
            .then(function (result){
                if (result.code === 1 ) {
                    datas = result.payLoad;
                    datas.forEach(function(data){
                        var latLng = new google.maps.LatLng(data.location.lat, data.location.lng);
                        if(data.area !== null ) {
                            $scope.heatmapData.push({location: latLng, weight: data.price / (data.area * 1000)});
                        }
                    });
                } else {
                    alert("Failed to load data");
                }
            });

        console.log($scope.heatmapData);

// Heatmap data: 500 Points

    });