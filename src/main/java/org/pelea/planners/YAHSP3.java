/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2012, Mois?s Mart?nez
 *
 * (Questions/bug reports now to be sent to Mois?s Mart?nez)
 *
 * This file is part of Pelea.
 * 
 * Pelea is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Pelea is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Pelea.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ************************************************************************/

package org.pelea.planners;

import org.pelea.planner.Planner;

public class YAHSP3 extends Planner {

    public YAHSP3(String id) {
        super(id, "YAHSP3", true);
        this.output = false;
        this._result_files = new String[1];
        this._result_file_base = this.temp + "plan_final.tmp";
        this._result_files[0] = this._result_file_base;
    }

    @Override
    public String getRePlanH(String domain, String problem, String plan) throws Exception {
        return this.getPlanH(domain, problem);
    }

    @Override
    public String getComand(String domain, String problem, int type) throws Exception {
        if (System.getProperty("os.name").contains("win")) {
            return "Windows does not supported";
        }
        else {
            return this.path + "cpt-yahsp/release/cpt_yahsp" + " -y " + 1 + " -o " + domain + " -f " + problem + " -out " + this._result_file_base;
        }
    }

    @Override
    public String getComand(String domain, String problem, String plan, int type) throws Exception {
        if (System.getProperty("os.name").contains("win")) {
            return "Windows does not supported";
        }
        else {
            return this.path + "cpt-yahsp/release/cpt_yahsp" + " -y " + 1 + " -o " + domain + " -f " + problem + " -out " + this._result_file_base;
        }
    }
}
