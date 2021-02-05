# Wait for new Version GitHub Action

Makes GET requests to given server and check response status and optionally commit sha to detect if new version deployed.

## Inputs

### `url`

The URL to poll. Default `"http://localhost/"`

### `responseCode`

Response code to wait for. Default `"200"`

### `timeout`

Timeout in seconds. Default `"30"`. Can be float `'30.5'` (30,5 seconds)

### `interval`

Interval between polling in seconds. Default `"1"`. Can be float `'0.2'` (200 ms)
        default: 200

### `commitSha`
Optional check for commit SHA of deployed version. This check is Spring Boot Actuator specific.

## Example usage
```
uses: kyberorg/wait_for_new_version@v1
with:
  url: 'http://localhost:8081/'
  responseCode: 200
  timeout: 20
  interval: 0.5
```
