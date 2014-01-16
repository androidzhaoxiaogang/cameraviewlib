
package com.android.cameralib;

import android.hardware.Camera;
import android.view.View;
import java.io.IOException;

/**
 * The Interface PreviewHandler.
 */
public interface PreviewHandler {
  
  /**
   * Set the camera preview display.
   *
   * @param camera the camera
   * @throws IOException Signals that an I/O exception has occurred.
   */
  void display(Camera camera) throws IOException;

  /**
   * Gets the camera preview.
   *
   * @return the preview
   */
  View getPreview();
}
