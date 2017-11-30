
angular.module('app.controllers').
controller('SearchResultController', function ($scope, $rootScope,$cacheFactory, SearchResults, cribsFactory, favoriteFactory, QueryServices, NgMap) {

    $scope.cribs;

    $scope.favorites;

    $scope.saveDisabled =false;

    // var cache = $cacheFactory('realtor');


    var vm = this;

    vm.user = null;
    $scope.priceInfo = {
        min:0,
        max:1000000
    };

    NgMap.getMap().then(function (map) {
        $scope.map = map;
    });

    $scope.showProp = function(event, prop) {
        $scope.selectedProp = prop;
        $scope.map.showInfoWindow('myInfoWindow',prop.propertyId.toString());
    };

    // cribsFactory.getCribs().then(function (data) {
    //     $scope.cribs=data.data;
    // }, function(error) {
    //     console.log(error);
    // });

    function getSearchResult(){
        $scope.cribs = SearchResults.getProperty();
    }

    getSearchResult();

// favoriteFactory.getFavorites().then(function (favorites) {
//     $scope.favorites = favorites.data;
// }, function(error) {
//     console.log(error);
// });

    initController();
    function initController() {
        QueryServices.getUserByNameCustom($rootScope.globals.currentUser.name)
            .then(function (result) {
                if (result.payLoad[0].name === $rootScope.globals.currentUser.name) {
                    vm.user = result.payLoad[0];
                    QueryServices.getFavoriteByUidCustom(result.payLoad[0].id)
                        .then(function (result){
                            if (result.code === 1 ) {
                                $scope.favorites = result.payLoad;
                            } else {
                                alert("Failed to load favorite list");
                            }
                        });
                } else {
                    vm.user = $rootScope.globals.currentUser;
                    console.log("cannot map");

                }
            });

    }

// QueryServices.getProperty(
//     {
//         'price':'{lt:500000}',
//         'year':'{gt:1990}',
//         'type': 'House',
//         'area': '{gt:1000}',
//         'limit': 1
//     }
// ).then(function (response){
//     //console.log(response.payLoad);
// });

    $scope.createFavorite = function (crib) {
        var param = {uid: vm.user.id ,propertyid: crib.propertyId};
        console.log(param);
        QueryServices.createFavorite(param).then(function (result){
            if(result.code === 1){
                console.log(result.message);
                $scope.favorites.push(crib);
                $scope.saveDisabled = true;
            } else{
                alert("Failed to save to favorite list");
            }
        });
    };

    $scope.removeFavorite = function (favorite) {
        var param = {uid: vm.user.id ,propertyid: favorite.propertyId};
        console.log(param);
        QueryServices.removeFavorite(param).then(function (result){
            if(result.code === 1){
                var index = $scope.favorites.indexOf(favorite);
                $scope.favorites.splice(index,1);
            } else{
                alert("Failed to remove from favorite list");
            }
        });
    };
});

