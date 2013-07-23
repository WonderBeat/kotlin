package org.jetbrains.jet.lang.psi.stubs.builder;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.impl.compiled.ClsStubBuilderFactory;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.util.cls.ClsFormatException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.descriptors.serialization.*;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.lang.psi.stubs.elements.JetStubElementTypes;
import org.jetbrains.jet.lang.psi.stubs.impl.PsiJetClassStubImpl;
import org.jetbrains.jet.lang.psi.stubs.impl.PsiJetFileStubImpl;
import org.jetbrains.jet.lang.psi.stubs.impl.PsiJetFunctionStubImpl;
import org.jetbrains.jet.lang.resolve.java.resolver.KotlinClassFileHeader;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.lang.resolve.name.Name;

import java.util.Collections;

public class JetClsStubBuilderFactory extends ClsStubBuilderFactory<JetFile> {
    @Nullable
    @Override
    public PsiFileStub<JetFile> buildFileStub(VirtualFile file, byte[] bytes) throws ClsFormatException {
        KotlinClassFileHeader header = KotlinClassFileHeader.readKotlinHeaderFromClassFile(file);
        ClassData classData = header.readClassData();
        if (classData != null) {
            //TODO: these are not correct fq names
            FqName packageFqName = header.getJvmClassName().getFqName().parent();
            FqName classFqName = header.getJvmClassName().getFqName();
            return createStubForClassData(classData, packageFqName, classFqName);
        }
        return null;
    }

    @NotNull
    private static PsiJetFileStubImpl createStubForClassData(@NotNull ClassData classData, FqName packageFqName, FqName classFqName) {
        ProtoBuf.Class classProto = classData.getClassProto();
        NameResolver nameResolver = classData.getNameResolver();
        PsiJetFileStubImpl fileStub = new PsiJetFileStubImpl(null, packageFqName.asString(), packageFqName.isRoot());
        Name className = nameResolver.getName(classProto.getName());
        ProtoBuf.Class.Kind kind = Flags.CLASS_KIND.get(classProto.getFlags());
        boolean isTrait = kind == ProtoBuf.Class.Kind.TRAIT;
        boolean isEnumClass = kind == ProtoBuf.Class.Kind.ENUM_CLASS;
        boolean isEnumEntry = kind == ProtoBuf.Class.Kind.ENUM_ENTRY;
        boolean isAnnotationClass = kind == ProtoBuf.Class.Kind.ANNOTATION_CLASS;
        PsiJetClassStubImpl classStub =
                new PsiJetClassStubImpl(JetStubElementTypes.CLASS, fileStub, classFqName.asString(),
                                        className.asString(),
                                        Collections.<String>emptyList(), isTrait, isEnumClass, isEnumEntry, isAnnotationClass,
                                        /*TODO: is inner class = */ false);
        for (ProtoBuf.Callable callableProto : classProto.getMemberList()) {
            new PsiJetFunctionStubImpl(JetStubElementTypes.FUNCTION, classStub, nameResolver.getName(callableProto.getName()).asString(),
                                       false, null, callableProto.getReceiverType() != null);
        }
        return fileStub;
    }

    @Override
    public boolean canBeProcessed(VirtualFile file, byte[] bytes) {
        return true;
    }

    @Override
    public boolean isInnerClass(VirtualFile file) {
        //NOTE: copy of DefaultClsStubBuilderFactory#isInnerClass
        //NOTE: it seems we are not going to process any inner classes
        String name = file.getNameWithoutExtension();
        int len = name.length();
        int idx = name.indexOf('$');

        while (idx > 0) {
            if (idx + 1 < len && Character.isDigit(name.charAt(idx + 1))) return true;
            idx = name.indexOf('$', idx + 1);
        }
        return false;
    }
}
