package com.zrlog.test.model;

import com.zrlog.model.Tag;
import com.zrlog.test.TestApplication;
import org.junit.Test;

public class TestTag extends TestApplication {

    @Test
    public void testUpdate() {
        new Tag().update("java,java ", "C#,C# ");
    }
}
