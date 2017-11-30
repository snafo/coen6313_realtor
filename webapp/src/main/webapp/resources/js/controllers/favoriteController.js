
angular.module('app.controllers').
controller('FavoriteController', function ($scope, $rootScope, QueryServices, NgMap) {
    $rootScope.currentTab = 'favorite';

    $scope.cribs;

    $scope.favorites = true;

    $scope.saveDisabled =false;

    $scope.user = {};

    NgMap.getMap().then(function (map) {
        $scope.map = map;
    });

    $scope.showProp = function(event, prop) {
        $scope.selectedProp = prop;
        $scope.map.showInfoWindow('myInfoWindow',prop.propertyId.toString());
    };

    initController();
    function initController() {
        QueryServices.getUserByNameCustom($rootScope.globals.currentUser.name)
            .then(function (result) {
                if (result.payLoad[0].name === $rootScope.globals.currentUser.name) {
                    $scope.user = result.payLoad[0];
                    QueryServices.getFavoriteProperty(result.payLoad[0].id)
                        .then(function (result){
                            if (result.code === 1 ) {
                                $scope.cribs = result.payLoad;
                            } else {
                                alert("Failed to load recommend list");
                            }
                        });
                } else {
                    // $scope.user = $rootScope.globals.currentUser;
                    // console.log("cannot map");
                }
            });
    }

    $scope.removeFavorite = function (favorite) {
        var param = {uid: $scope.user.id ,propertyid: favorite.propertyId};
        QueryServices.removeFavorite(param).then(function (result){
            if(result.code === 1){
                var index = $scope.cribs.indexOf(favorite);
                $scope.cribs.splice(index,1);
            } else{
                alert("Failed to remove from favorite list");
            }
        });
    };
});

