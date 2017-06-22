/*
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swinghelper.debug;

import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * This class is used to detect Event Dispatch Thread rule violations. 
 * See <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</a> for more info. This is a modification of original idea of
 * Scott Delap<br>
 * Initial version of ThreadCheckingRepaintManager can be found here:
 * <a href="http://www.clientjava.com/blog/2004/08/20/1093059428000.html">Easily
 * Find Swing Threading Mistakes</a>
 * 
 * @author Scott Delap
 * @author Alexander Potochkin
 */
public class CheckThreadViolationRepaintManager extends RepaintManager {
  // it is recommended to pass the complete check
  private boolean completeCheck = true;

  private WeakReference<JComponent> lastComponent;

  public CheckThreadViolationRepaintManager(boolean completeCheck) {
    this.completeCheck = completeCheck;
  }

  public CheckThreadViolationRepaintManager() {
    this(true);
  }

  public boolean isCompleteCheck() {
    return completeCheck;
  }

  public void setCompleteCheck(boolean completeCheck) {
    this.completeCheck = completeCheck;
  }

  public synchronized void addInvalidComponent(JComponent component) {
    checkThreadViolations(component);
    super.addInvalidComponent(component);
  }

  public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
    checkThreadViolations(component);
    super.addDirtyRegion(component, x, y, w, h);
  }

  private void checkThreadViolations(JComponent c) {
    if(!SwingUtilities.isEventDispatchThread()
        && (completeCheck || c.isShowing())) {
      boolean repaint = false;
      boolean fromSwing = false;
      boolean imageUpdate = false;
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      for(StackTraceElement st : stackTrace) {
        if(repaint && st.getClassName().startsWith("javax.swing.") &&
        // for details see
        // https://swinghelper.dev.java.net/issues/show_bug.cgi?id=1
            !st.getClassName().startsWith("javax.swing.SwingWorker")) {
          fromSwing = true;
        }
        if(repaint && "imageUpdate".equals(st.getMethodName())) {
          imageUpdate = true;
        }
        if("repaint".equals(st.getMethodName())) {
          repaint = true;
          fromSwing = false;
        }
      }
      if(imageUpdate) {
        // assuming it is java.awt.image.ImageObserver.imageUpdate(...)
        // image was asynchronously updated, that's ok
        return;
      }
      if(repaint && !fromSwing) {
        // no problems here, since repaint() is thread safe
        return;
      }
      // ignore the last processed component
      if(lastComponent != null && c == lastComponent.get()) { return; }
      lastComponent = new WeakReference<JComponent>(c);
      violationFound(c, stackTrace);
    }
  }

  protected void violationFound(JComponent c, StackTraceElement[] stackTrace) {
    System.out.println();
    System.out.println("EDT violation detected");
    System.out.println(c);
    for(StackTraceElement st : stackTrace) {
      System.out.println("\tat " + st);
    }
  }
}