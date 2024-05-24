export type TemplateCenterData = {
    url: string;
};

const TemplateCenter = ({ data }: { data: TemplateCenterData }) => {
    return <iframe width="100%" style={{ border: 0 }} height={1200} src={data.url} />;
};

export default TemplateCenter;
