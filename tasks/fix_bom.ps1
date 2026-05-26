$base = "C:\Users\sxp267\IdeaProjects\holon-vaadin-flow\demo\src\main\java\com\holonplatform\vaadin\flow\demo\ui\views"
$files = @(
    "ListingBundleDemoView.java",
    "MenuBarDemoView.java",
    "AlertModalDemoView.java",
    "PageSizeSelectorDemoView.java",
    "PropertyListingDemoView.java",
    "TooltipDemoView.java",
    "LayoutDemoView.java"
)

foreach ($f in $files) {
    $path = Join-Path $base $f
    if (-not (Test-Path $path)) {
        Write-Host "NOT FOUND: $f"
        continue
    }
    $bytes = [System.IO.File]::ReadAllBytes($path)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $stripped = $bytes[3..($bytes.Length - 1)]
        [System.IO.File]::WriteAllBytes($path, $stripped)
        Write-Host "BOM removed: $f"
    } else {
        Write-Host "No BOM:      $f  (first byte=$($bytes[0]))"
    }
}
Write-Host "Done."
