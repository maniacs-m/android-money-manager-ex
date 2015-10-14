/*
 * Copyright (C) 2012-2015 The Android Money Manager Ex Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.money.manager.ex.domainmodel;

/**
 * Mapping between Asset Classes and Stocks.
 */
public class AssetClassStock
    extends EntityBase {

    public static final String ID = "ID";
    public static final String ASSETCLASSID = "ASSETCLASSID";
    public static final String STOCKID = "STOCKID";

    public static AssetClassStock create() {
        AssetClassStock entity = new AssetClassStock();
        return entity;
    }

    public AssetClassStock() {
        super();
    }

    public void setId(int value) {
        setInteger(ID, value);
    }

    public Integer getAssetClassId() {
        return getInteger(ASSETCLASSID);
    }

    public void setAssetClassId(int value) {
        setInteger(ASSETCLASSID, value);
    }

    public Integer getStockId() {
        return getInteger(STOCKID);
    }

    public void setStockId(int value) {
        setInteger(STOCKID, value);
    }
}