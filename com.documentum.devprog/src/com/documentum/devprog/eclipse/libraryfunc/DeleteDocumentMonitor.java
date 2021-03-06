/*******************************************************************************
 * Copyright (c) 2005-2006, EMC Corporation 
 * All rights reserved.

 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided that 
 * the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * - Neither the name of the EMC Corporation nor the names of its 
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *******************************************************************************/

/*
 * Created on Aug 8, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;

import com.documentum.devprog.eclipse.common.PluginState;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.documentum.operations.IDfDeleteOperation;

public class DeleteDocumentMonitor extends OperationProgressMonitor implements
		IRunnableWithProgress {

	private IDfId docId = null;

	public DeleteDocumentMonitor(IDfId id) {
		docId = id;
	}

	public void run(IProgressMonitor mon) {
		super.setMonitor(mon);

		IDfSession sess = null;
		try {

			sess = PluginState.getSessionById(docId);
			IDfSysObject obj = (IDfSysObject) sess.getObject(docId);

			mon.beginTask("Deleting " + obj.getObjectName(), 100);
			IDfDeleteOperation oper = PluginState.getClientX()
					.getDeleteOperation();
			oper.setOperationMonitor(this);
			oper.add(obj);
			if (docId.getTypePart() == IDfId.DM_FOLDER
					|| (docId.getTypePart() == IDfId.DM_CABINET)) {
				oper.setDeepFolders(true);
			}

			super.setOperation(oper);

			if (oper.execute()) {
				System.out.println("delete oper succeeded");
				setSuccess(true);
			} else {
				System.out.println("deletefailed");
				setSuccess(false);
			}
			mon.done();
		} catch (DfException dfe) {
			DfLogger.error(this, "Delete failed", null, dfe);
			addErrors(dfe.getMessage());

		} finally {
			PluginState.releaseSession(sess);
		}

	}

}
