    [CmdletBinding()]
    param(
        $basicAuth,
        $baseUrl,
        $tokenUrl,
        $grandType,
        $password,
        $username,
        $planId
    )
    Write-Host "Set Execution parameters"
    $fullTokenUrl = "$( $tokenUrl )grant_type=$( $grandType )&username=$( $username )&password=$( $password )"
    Write-Host "Token Url :"$fullTokenUrl
    $tokenHeaders = New-Object "System.Collections.Generic.Dictionary[[String],[String]]"
    $tokenHeaders.Add("Authorization", "Basic $( $basicAuth )")
    Write-Host "TokenHeaders bilgisi :"$tokenHeaders
    $tokenResponse = Invoke-RestMethod $fullTokenUrl  -Method 'POST' -Headers $tokenHeaders
    $accessToken = $tokenResponse.access_token
    Write-Host  "Response access token:"$accessToken
    $fullRunPlanUrl = "$( $baseUrl )/api/plans/$( $planId )/run"
    Write-Host "Plan Url:"$fullRunPlanUrl
    $runHeaders = New-Object "System.Collections.Generic.Dictionary[[String],[String]]"
    $runHeaders.Add("Authorization", "Bearer $accessToken")
    $runPlanResponse = Invoke-RestMethod $fullRunPlanUrl -Method 'GET' -Headers $runHeaders
    $executionId = $runPlanResponse.execution_id
    $runResult = $runPlanResponse.successful
    Write-Host "Run plan response:"$runResult
    Write-Host "Execution Id:"$executionId

    if ($runResult) {
        Write-Output "Sonuc Url:https://testinium.io/Testinium.RestApi/api/executions/$executionId"
        while ($true) {
            $executionResult = Invoke-RestMethod "https://testinium.io/Testinium.RestApi/api/executions/$executionId" -Method 'GET' -Headers $runHeaders
            if (Get-Member -inputobject $executionResult -name "test_result_status_counts") {
                $executionResultCounts = $executionResult.test_result_status_counts
                Write-Output "Testler Sonuçlandı"
                Write-Output $executionResult | ConvertTo-Json
                if ($executionResultCounts.Contains("ERROR")) {
                    throw "Test sonuçlarında hata var:"
                    Write-Output $executionResultCounts
                }
                else {
                    Write-Output "Testler  tamamlandı"
                    Write-Output $executionResultCounts
                    Break
                }
            }
            Write-Output "1 dakika sonra tekrar istek atılacak"
            Start-Sleep -s 60
        }
    }
    else {
        Write-Output "Plan Çalıştırılamadı"
        throw "Plan Çalıştırılamadı"
    }