import {FunctionComponent, RefObject, useEffect, useRef} from "react";
import {EditorView} from "@uiw/react-codemirror";

type SyncProps = {
    editorRef: RefObject<EditorView>;
    previewRef: RefObject<HTMLElement>;
}

const ScrollSync: FunctionComponent<SyncProps> = ({editorRef, previewRef}) => {
    const syncingRef = useRef(false);


    // 编辑器滚动同步预览
    useEffect(() => {
        if (!editorRef.current || !previewRef.current) return;

        const scrollDOM = editorRef.current.scrollDOM;

        console.info(scrollDOM)

        function onEditorScroll() {
            if (syncingRef.current) return;
            syncingRef.current = true;

            const scrollTop = scrollDOM.scrollTop;
            const scrollHeight = scrollDOM.scrollHeight;
            const clientHeight = scrollDOM.clientHeight;
            const scrollRatio = scrollTop / (scrollHeight - clientHeight);

            const preview = previewRef.current;
            if (preview === null) {
                return;
            }
            const previewScrollHeight = preview.scrollHeight - preview.clientHeight;
            preview.scrollTop = previewScrollHeight * scrollRatio;

            setTimeout(() => {
                syncingRef.current = false;
            }, 10);
        }

        scrollDOM.addEventListener('scroll', onEditorScroll);

        return () => {
            scrollDOM.removeEventListener('scroll', onEditorScroll);
        };
    }, []);

    // 预览滚动同步编辑器（同理）
    useEffect(() => {
        if (!editorRef.current || !previewRef.current) return;

        function onPreviewScroll() {
            if (syncingRef.current) return;
            syncingRef.current = true;

            const preview = previewRef.current;

            if (preview === null) {
                return;
            }
            const scrollTop = preview.scrollTop;
            const scrollHeight = preview.scrollHeight;
            const clientHeight = preview.clientHeight;
            const scrollRatio = scrollTop / (scrollHeight - clientHeight);

            if (!editorRef.current || !previewRef.current) return;
            const scrollDOM = editorRef.current.scrollDOM;
            const editorScrollHeight = scrollDOM.scrollHeight - scrollDOM.clientHeight;
            scrollDOM.scrollTop = editorScrollHeight * scrollRatio;

            setTimeout(() => {
                syncingRef.current = false;
            }, 10);
        }

        previewRef.current.addEventListener('scroll', onPreviewScroll);

        return () => {
            if (previewRef.current) {
                previewRef.current.removeEventListener('scroll', onPreviewScroll);
            }
        };
    }, []);

    return <>

    </>
}

export default ScrollSync;