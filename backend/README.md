# WallpaperID backend API

## Usage

### Create new record

```shell
# request CURL
curl -X "POST" "https://${URL}/app_hashes" \
     -H 'Content-Type: application/json' \
     -d $'{ "appHash": "test-app-hash", "visitorId": "test-visitor-id" }'

# response
{"ok":true,"duration":"16ms","count":"0"}
```

```js
// example with Axios
axios({
	"method": "POST",
	"url": "https://${URL}/app_hashes",
	"headers": {
		"Content-Type": "application/json",
		"Cookie": "__profilin=p%3Dt"
	},
	"data": {
		"appHash": "test-app-hash",
		"visitorId": "test-visitor-id"
	}
})

// example with fetch
fetch("https://{URL}/app_hashes", {
      "method": "POST",
      "headers": { "Content-Type": "application/json", },
      "body": { "appHash": "test-app-hash", "visitorId": "test-visitor-id" }
})
.then((res) => res.json())
.catch(console.error.bind(console));
```

Code is MIT licensed

Copyright 2021, FingerprintJS, Inc