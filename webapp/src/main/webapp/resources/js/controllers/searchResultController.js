
angular.module('app.controllers').
controller('SearchResultController', function ($scope, $rootScope, $cookies, SearchResults, cribsFactory, favoriteFactory, QueryServices, NgMap) {

    $scope.cribs;

    $scope.saveDisabled =false;

    $scope.user = {};

    NgMap.getMap().then(function (map) {
        $scope.map = map;
    });

    $scope.showProp = function(event, prop) {
        $scope.selectedProp = prop;
        $scope.map.showInfoWindow('myInfoWindow',prop.propertyId.toString());
    };

    function getSearchResult(){
        $scope.cribs = SearchResults.getProperty();
    }

    getSearchResult();

    initController();
    function initController() {
        QueryServices.getUserByNameCustom($rootScope.globals.currentUser.name)
            .then(function (result) {
                if (result.payLoad[0].name === $rootScope.globals.currentUser.name) {
                    $scope.user = result.payLoad[0];
                } else {
                    // $scope.user = $rootScope.globals.currentUser;
                    // console.log("cannot map");
                }
            });

    }

    $scope.createFavorite = function (crib) {
        var param = {uid: $scope.user.id ,propertyid: crib.propertyId};
        console.log(param);
        QueryServices.createFavorite(param).then(function (result){
            if(result.code === 1){
                var index = $scope.cribs.indexOf(crib);
                $scope.cribs[index].favorite = true;
            } else{
                alert("Failed to save to favorite list");
            }
        });
    };
});

