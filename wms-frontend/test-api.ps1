$filePath = "test.jpg"
$boundary = "------------------------$(Get-Date -Format 'yyyyMMddHHmmssfff')"

# 创建multipart/form-data内容
$fileContent = Get-Content -Path $filePath -Encoding Byte
$fileContentBase64 = [System.Convert]::ToBase64String($fileContent)

$body = @"
--$boundary
Content-Disposition: form-data; name="file"; filename="$(Split-Path $filePath -Leaf)"
Content-Type: image/jpeg

$fileContentBase64
--$boundary--
"@

# 发送请求
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/ai/recognize" `
                                 -Method POST `
                                 -Headers @{
                                     "Content-Type" = "multipart/form-data; boundary=$boundary"
                                 } `
                                 -Body $body `
                                 -UseBasicParsing
    
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Response: $($response.Content)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
}