package org.jetbrains.jet.lang.resolve.java.vfilefinder;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.name.FqName;

public interface VirtualFileFinder {

    //TODO: this method should have scope parameter
    @Nullable
    VirtualFile find(@NotNull FqName className, GlobalSearchScope scope);
}
