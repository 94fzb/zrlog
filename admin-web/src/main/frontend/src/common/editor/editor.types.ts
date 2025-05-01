import { ChangedContent} from "../../components/articleEdit/index.types";

export type MarkdownEditorProps = {
    height: any;
    onChange: (content: ChangedContent) => void;
    value?: string;
    loadSuccess?: (editor: any) => void;
    getContainer?: () => HTMLElement;
    fullscreen: boolean;
    content: string;
};

export type EditorDialogState = {
    open: boolean;
    title: string;
    type: DialogType;
}

export type DialogType = "image" | "video" | "file" | "link" | "code" | "table" | "help"
