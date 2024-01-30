param(
    $baseUrl,
    $planId,
    $username,
    $password
)
$Url = "$($baseUrl)/Testinium.RestApi/oauth/token"
Write-Output "BaseUrl Bilgisi" $Url
Write-Output "Userneme Bilgisi" $( $username )
Write-Output "Password Bilgisi" $( $password )
Write-Host "Userneme Bilgisi" $username
Write-Host "Password Bilgisi" $password
$Body = @{
    grant_type = "password"
    username = "$username"
    password = "$password"
    scope = "api"
}
$headers = @{
    'Authorization' = 'Basic Y2xpZW50MTpjbGllbnQx'
    'Accept' = 'application/json'
}
$tokenResponse = Invoke-RestMethod -Method 'Post' -Uri $url -Headers $headers -Body $body
Write-Output "Post İsteği Yapıldı"
$accessToken = $tokenResponse.access_token
Write-Host "Access Token Değeri"$accessToken
$fullRunPlanUrl = "$( $baseUrl )/api/plans/$( $planId )/run"
Write-Host "Plan Url:"$fullRunPlanUrl
$headerss = @{
    Authorization = "Bearer $accessToken"

}
$Urll = "https://dev.testinium.com/Testinium.RestApi/api/plans/$( $planId )/run"
$runPlanResponse = Invoke-RestMethod -Headers $headerss -Method Get -Uri $Urll
$executionId = $runPlanResponse.execution_id
$runResult = $runPlanResponse.successful
Write-Host "Run plan response:"$runResult
Write-Host "Execution Id:"$executionId
if ($runResult) {
    Write-Output "Sonuc Url:https://dev.testinium.com/Testinium.RestApi/api/testExecutions/$executionId/company/1"
    while ($true) {
        $executionResult = Invoke-RestMethod "https://dev.testinium.com/Testinium.RestApi/api/testExecutions/$executionId/company/1" -Method 'GET' -Headers $headerss
        if (Get-Member -inputobject $executionResult -name "test_result_status_counts") {
            $executionResultCounts = $executionResult.test_result_status_counts
            Write-Output "Testler Sonuçlandı"
            Write-Output $executionResult | ConvertTo-Json
            if ($executionResultCounts.Contains("ERROR")) {
                throw "Test sonuçlarında hata var:" + $executionResultCounts
            }
            else {
                Write-Output "Testler Başarıyla tamamlandı"
                Write-Output $executionResultCounts
                Break
            }
        }
        Write-Output "10 saniye sonra tekrar istek atılacak"
        Start-Sleep -s 10
    }
}
else {
    Write-Output "Plan Çalıştırılamadı"
    throw "Plan Çalıştırılamadı"
}