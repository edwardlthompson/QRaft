# LineageOS lock-screen widget

QRaft’s Glance widget declares `widgetCategory="home_screen|keyguard"`.

1. Save a Website profile in the app (Editor → Save Website profile).
2. On the home screen: long-press → Widgets → **QRaft QR**. Pick the profile in the config dialog.
3. On LineageOS Glanceable Hub / lock-screen widgets (wording varies by version): add the same **QRaft QR** widget.
4. Tap the widget for a full-brightness QR. Tap the image to close (brightness restores).
5. Optional: pull down Quick Settings and add the **Brighten QR** tile — it opens the same full-brightness view for the selected gallery card.
6. **Next** cycles saved profiles. Sensitive profiles show **Hidden** until you unlock with PIN / biometric and open Brighten.
7. Config dialog shows home vs lock-screen pin steps. Caption uses the gallery card’s style caption when set.

A lock-screen QR is visible to anyone who can see the phone. Do not pin Wi-Fi passwords there unless you accept that.
