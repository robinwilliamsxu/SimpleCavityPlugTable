/**
 * Copyright 2010 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example Java or Java Script plugins.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */

package table;

import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXAdditionalComponent;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLibraryCavityPlug;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.table.IXHarnessTable;
import com.mentor.chs.plugin.table.IXTableData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple example HarnessXC table for displaying part numbers of plugs for
 * connector cavities
 * @author davewa
 */
public class CavityPlugTable //implements IXHarnessTable, IXApplicationContextListener 
{

    /**
     * Only work on Connectors
     * @return IXConnector
     */
    public Class<? extends IXObject> getTableContext() {
        return IXConnector.class;
    }

    public void initialize(IXObject ixo) {
    }

    public IXTableData populate(IXObject ixo) {
        IXConnector conn = null;
        if (ixo instanceof IXDiagramObject) {
            conn = (IXConnector) ((IXDiagramObject) ixo).getConnectivity();
        } else {
            conn = (IXConnector) ixo;
        }
        if (conn != null) {
            final List<IXCavity> pluggedCavities = new ArrayList<IXCavity>();
            final Map<IXCavity,String> cavityMap = new HashMap<IXCavity, String>();
            for (IXAbstractPin cav : conn.getPins()) {
                IXCavity cavity = (IXCavity) cav;
                // The only way to identify  plug is by checkng the additional components
                // against the Library.
                // 2010.2 contains a new method on IXCavity called getCavityPlug() which makes
                // All of this code redundant
                if (context != null) {
                    for (IXAdditionalComponent ad : cavity.getAdditionalComponents()) {
                        String pn = ad.getAttribute("PartNumber");
                        IXLibraryObject libObj = context.getLibrary().getLibraryObject(pn);
                        if (libObj != null && libObj instanceof IXLibraryCavityPlug) {
                            pluggedCavities.add(cavity);
                            cavityMap.put(cavity,pn);
                        }
                    }
                }
            }
            // Sort the cavities alphanumerically
            Collections.sort(pluggedCavities, new AlphaNumComparator<IXCavity>());
            // return the table model
            return new IXTableData() {

                public int getRowCount() {
                    return pluggedCavities.size();
                }

                public Object getCellValueAt(int row, int col) {
                    IXCavity cav = pluggedCavities.get(row);
                    if (col == 0) {
                        return cav.getAttribute("Name");
                    }
                    return cavityMap.get(cav);
                }

                public int getColumnCount() {
                   return 2;
                }

                public int getHeaderCount() {
                    return 1;
                }

                public Object getHeaderValueAt(int i, int col) {
                    if (col == 0) {
                        return "Cavity";
                    }
                    return "Plug";
                }
            };
        }
        return null;
    }

    public String getDescription() {
        return "Displays Plugged Connector Cavities Only";
    }

    public String getName() {
        return "Connector Cavity Plug Table";
    }

    public String getVersion() {
        return "1";
    }
    private IXApplicationContext context = null;

    public void setApplicationContext(IXApplicationContext ixac) {
        context = ixac;
    }
}
