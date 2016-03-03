var baseUrl = "";
var restUrl = baseUrl + "/rest/v1";

var cardServices = angular.module('cardServices', ['ngResource']);
var eventServices = angular.module('eventServices', ['ngResource']);
var loginServices = angular.module('loginServices', ['ngResource']);
var pageServices = angular.module('pageServices', ['ngResource']);
var postServices = angular.module('postServices', ['ngResource']);
var userServices = angular.module('userServices', ['ngResource']);

var transform = function(data){
    return $.param(data);
};

loginServices.factory('Login', ['$resource', function($resource) {
    return $resource(baseUrl + "/rest/login", {}, {
        login: {
            method: 'POST',
            isArray: false,
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: transform
        }
    })
}]);

loginServices.factory('Event', ['$resource', function($resource) {
    return $resource(url('/event'), {}, {

    });
}]);

userServices.factory('User', ['$resource', function($resource) {
    return $resource(url("/users/:userId"), {}, {
        create: {
            url: url("/users"),
            method: 'POST',
            isArray: false
        },
        find: {
            method: 'GET',
            isArray: false,
            params: {userId: 'me'}
        },
        logout: {
            url: baseUrl + "/rest/logout",
            method: 'POST'
        }
    })
}]);

cardServices.factory('Card', ['$resource', function($resource) {
    return $resource(url("/cards"), {}, {
        create: {
            method: 'POST',
            isArray: false
        },
        all: {
            method: 'GET',
            isArray: true
        }
    })
}]);

pageServices.factory('Page', ['$resource', function($resource) {
    return $resource(url('/pages'), {}, {
        create: {
            method: 'POST',
            isArray: false
        },
        query: {
            url: url('/pages/:pageId'),
            method: 'GET',
            isArray: false
        }
    })
}]);

function url(v) {
    return restUrl + v;
}