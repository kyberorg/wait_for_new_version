# Wait for new Version GitHub Action

Makes GET requests to given server and check response status and optionally commit sha to detect if new version deployed.

## Inputs

### `url`

The URL to poll. Default `"http://localhost:8080"`

### `responseCode`

Response code to wait for. Default `"200"`

### `timeout`

Timeout in seconds. Default `"30"`. Can be float `'30.5'` (30,5 seconds)

### `interval`

Interval between polling in seconds. Default `"1"`. Can be float `'0.2'` (200 ms)
        default: 200

### `hasActuator`
Determines if application has Spring Boot Actuator. Values: `true/false`. 
If `true` - action will perform Actuator specific status or version check in addition to response code check. 

### `commitSha`
Optional check for commit SHA of deployed version. This check is Spring Boot Actuator specific.
Has no effect when `hasActuator` set to false.
## Example usage
```
uses: kyberorg/wait_for_new_version@v1.0.1
with:
  url: 'http://localhost:8081'
  responseCode: 200
  timeout: 20
  interval: 0.5
  hasActuator: true
  commitSha: 9a9b9c
```
