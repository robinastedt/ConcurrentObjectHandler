package com.astedt.robin.concurrentobjecthandler;

import java.util.List;

/*
@author Robin Åstedt
USAGE:
 * README.md for info
*/


public interface ConcurrentObject {
    public void read(List<ConcurrentObject> objectList);
    public void write();
}