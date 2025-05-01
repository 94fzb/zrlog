// Add the icons to the library so you can use it in your page
import { dom, library } from "@fortawesome/fontawesome-svg-core";
import {
    fa2,
    fa3,
    fa4,
    faBold,
    faClipboard,
    faClose,
    faEye,
    faEyeSlash,
    faFileCode,
    faFileVideo,
    faImage,
    faInfoCircle,
    faItalic,
    faLink,
    faListOl,
    faListUl,
    faMinus,
    faPaperclip,
    faPhotoFilm,
    faQuestionCircle,
    faQuoteLeft,
    faStrikethrough,
    faTable,
} from "@fortawesome/free-solid-svg-icons";

const icons = [
    faBold,
    faStrikethrough,
    fa4,
    faItalic,
    faQuoteLeft,
    faListUl,
    faListOl,
    faMinus,
    faQuestionCircle,
    faInfoCircle,
    faClipboard,
    faTable,
    faFileCode,
    faPaperclip,
    faFileVideo,
    fa3,
    fa2,
    faImage,
    faPhotoFilm,
    faLink,
    faClose,
    faEyeSlash,
    faEye,
];
icons.forEach((e) => {
    library.add(e);
});

// This will replace any existing `<i>` elements with `<svg>` and set up a MutationObserver to continue doing this as the DOM changes.
dom.watch();

const EditorIcon = ({ name, onClick }: { name: string; onClick?: () => void }) => {
    return (
        <div
            onClick={onClick}
            className={"editor-icon"}
            style={{
                cursor: "pointer",
                minWidth: 34,
                display: "flex",
                alignItems: "center",
                color: "rgb(119, 119, 119)",
                fontSize: 16,
                height: 38,
                borderRadius: 6,
                justifyContent: "center",
            }}
        >
            <i className={"fa fa-" + name}></i>
        </div>
    );
};
export default EditorIcon;
